# Разрабатываем фичу: Speed Test. Android.

В одном из наших проектов мы столкнулись с необходимостью реализовать возможность измерения пропускной способности интернет-канала, так называемого спидтеста. 
Спидтест – это максимальное число данных, принятых устройством, либо переданных через канал передачи данных за определенную единицу времени.

Рассмотрим в чём измеряется пропускная способность интернет-канала. Количество информации обычно измеряется в битах, байтах и т.д. Несмотря на то, что размер файлов на жестком диске указывается обычно в байтах (Кбайт, Мбайт, Гбайт), при измерении пропускной способности используются Биты за Секунду и производные: 

* килобиты в секунду – кбит/с (kbps, kbit/s или kb/s), 
* мегабиты в секунду – Мбит/с (Mbps, Mbit/s или Mb/s), 
* гигабиты в секунду – Гбит/с (Gbps, Gbit/s или Gb/s).

Интернет-провайдеры используют для подсчёта скорости передачи данных, международную систему единиц (СИ), где 1 килобайт равняется 1000 битам, а не 1024.

В двух словах, процесс измерения пропускной способности интернет-канала выглядит, как подсчёт количества байт, успешно переданных с устройства и принятых на устройство. Есть несколько способов реализации этого процесса. Рассмотрим первый способ с использованием библиотеки [OkHttp](https://github.com/square/okhttp) от команды Square.

## Измерение скорости передачи данных с помощью библиотеки OkHttp

Создадим пакет datasource, в котором у нас будут находиться классы, относящиеся непосредственно к подсчёту скорости передачи данных. Начнём с измерения скорости передачи данных на удаленный сервер (download speed).

#### OkHttp. Получение данных Download

Перейдём к разбору логики спидтеста, для этого нам необходимо посчитать, сколько бит в секунду мы можем передать, либо принять через интернет-канал. Для этого, мы будем использовать библиотеку OkHttp, она позволяет подсчитывать количество передаваемых и получаемых байт.

Для подсчёта передаваемых на устройство байт (download speed), нам необходимо создать OkHttpClient с кастомным Interceptor-ом. Interceptor в библиотеке OkHttp – перехватчик, класс, который следит за сетевыми запросами и ответами.  Кастомный Interceptor нам необходимо изменить так, чтобы он использовал наш, переопределенный ResponseBody.  

~~~ kotlin
OkHttpClient.Builder()
    .addNetworkInterceptor { chain ->
        val originalResponse = chain.proceed(chain.request())
        val originalBody = originalResponse.body()
        originalBody?.let {
            originalResponse.newBuilder()
                .body(SpeedTestDownloadResponseBody(
                    responseBody = it,
                    speedTestListener = listener,
                    startTimeMillis = timeBenchmark.build(),
                    timeBenchmark = timeBenchmark,
                    reportInterval = reportInterval
                ))
                .build()
        }
    }
    .build()
~~~

В нашем переопределенном ResponseBody мы создаём анонимный класс, унаследованный от ForwardingSource. Этот класс содержит в себе метод read, возвращающий количество подсчитанных байт. В теле нашего метода read мы подсчитываем количество прочитанных байт и передаём эту информацию с помощью SpeedTestListener.

~~~ kotlin
class SpeedTestDownloadResponseBody(
        private val responseBody: ResponseBody,
        private val speedTestListener: SpeedTestListener,
        private val startTimeMillis: Long,
        private val timeBenchmark: TimeBenchmark,
        private val reportInterval: Long
) : ResponseBody() {

    private val bufferedSource = Okio.buffer(initSource(responseBody.source()))

    @Throws(IOException::class)
    override fun contentLength(): Long =
            responseBody.contentLength()

    override fun contentType(): MediaType? =
            responseBody.contentType()

    override fun source(): BufferedSource {
        return bufferedSource
    }

    @Throws(IOException::class)
    private fun initSource(source: Source): Source =
            object : ForwardingSource(source) {
                var totalBytesRead: Long = 0L

                override fun read(sink: Buffer, byteCount: Long): Long {
                    var bytesRead = 0L
                    try {
                        bytesRead = super.read(sink, byteCount)
                        totalBytesRead += if (bytesRead != -1L) bytesRead else 0

                        if (bytesRead == -1L) {
                            speedTestListener.onNext(getZeroModel(bytesRead == -1L))
                            speedTestListener.onComplete()
                        }

                        if (timeBenchmark.checkExpiredTime(reportInterval))
                            speedTestListener.onNext(FileTransferModel(
                                    totalBytesRead = totalBytesRead,
                                    contentLength = responseBody.contentLength(),
                                    isDone = bytesRead == -1L,
                                    startTimeMillis = startTimeMillis,
                                    fileTransferMarker = FileTransferMarker.DOWNLOAD))

                    } catch (e: Exception) {
                        speedTestListener.onNext(getZeroModel())
                        throw e
                    }
                    return bytesRead
                }
            }

    private fun getZeroModel(isDone: Boolean = false): FileTransferModel =
            FileTransferModel(0, 0, isDone, startTimeMillis, FileTransferMarker.NONE)

}
~~~

В конструктор переопределенного ResponseBody мы передаём анонимный класс, реализующий интерфейс SpeedTestListener, с помощью которого мы сможем передавать информацию о количестве подсчитанных байт и состоянии процесса выполнения.

~~~ kotlin
interface SpeedTestListener {

    fun onComplete()

    fun onNext(model: FileTransferModel)

    fun onError(throwable: Throwable)
}
~~~

Интерфейс SpeedTestListener мы будем использовать как для измерения исходящей скорости передачи данных (upload), так и для измерения входящей скорости передачи данных (download).

#### OkHttp. Получение данных Upload 

Для подсчёта передаваемых на сервер с устройства байт (upload speed), нам необходимо создать OkHttpClient с кастомным Request-ом и RequestBody.

Первое что необходимо сделать для подсчёта передаваемых на сервер байт – это создать кастомный RequestBody. Вызовем builder у MultipartBody, укажем тип RequestBody – MultipartBody.FORM. Далее, установим хедеры и передадим кастомный SpeedTestUploadRequestBody, код которого мы скоро разберём.

~~~ kotlin
MultipartBody.Builder()
    .setType(MultipartBody.FORM)
    .addPart(
        Headers.of("Content-Disposition", "form-data; name=\"image\"; filename=\"${file.name}\""),
        SpeedTestUploadRequestBody(
                file = file,
                speedTestListener = listener,
                contentType = CONTENT_TYPE,
                timeBenchmark = timeBenchmark,
                reportInterval = reportInterval
        )
    )
    .build()
~~~

Необходимо установить наш переопределенный RequestBody экземпляру класса Request, как POST метод:

~~~ kotlin
Request.Builder()
    .url(url)
    .post(requestBody)
    .build()
~~~
 
В конструктор переопределенного RequestBody мы, так же, как и для измерения download, передаём анонимный класс, реализующий SpeedTestListener.

~~~ kotlin
class SpeedTestUploadRequestBody(
        private val file: File,
        private val contentType: String,
        private val speedTestListener: SpeedTestListener,
        private val timeBenchmark: TimeBenchmark,
        private val reportInterval: Long
) : RequestBody() {

    private val startTimeMillis: Long = timeBenchmark.build()

    companion object {
        const val OKIO_SEGMENT_SIZE = 2048L
    }

    override fun contentType(): MediaType? =
            MediaType.parse(contentType)

    override fun writeTo(sink: BufferedSink) {
        Okio.source(file).use { it ->
            var totalBytesRead: Long = 0
            try {
                while (true) {
                    val read = it.read(sink.buffer(), OKIO_SEGMENT_SIZE)
                    if (read == -1L) {
                        speedTestListener.onNext(getZeroModel(read == -1L))
                        speedTestListener.onComplete()
                        break
                    }
                    totalBytesRead += read
                    sink.flush()
                    if (timeBenchmark.checkExpiredTime(reportInterval))
                        speedTestListener.onNext(FileTransferModel(
                                totalBytesRead = totalBytesRead,
                                contentLength = this.contentLength(),
                                isDone = read == -1L,
                                startTimeMillis = startTimeMillis,
                                fileTransferMarker = FileTransferMarker.UPLOAD))
                }
            } catch (e: Exception) {
                speedTestListener.onNext(getZeroModel())
                throw e
            }
        }

    }

    private fun getZeroModel(isDone: Boolean = false): FileTransferModel =
            FileTransferModel(0, 0, isDone, startTimeMillis, FileTransferMarker.NONE)
}
~~~

Переопределяем метод writeTo() в нашем кастомном RequestBody. В методе writeTo() мы подсчитываем количество переданных через интернет-канал байт и передаём эту информацию через SpeedTestListener в репозиторий.

В этих классах мы используем наши кастомные OkHttpClient. Также эти классы проксируют SpeedTestListener в  RequestBody и ResponseBody.

Реализуем классы OkHttpApiSpeedTest. В конструктор OkHttpApiSpeedTestDownload передадим фабрику, создающую нам экземпляры OkHttpClient.

~~~ kotlin
class OkHttpApiSpeedTestDownload {
...

    fun executeDownloadSpeedTest(url: String, speedTestListener: SpeedTestListener) {
        val request = okHttpFactory.buildDownloadRequest(url)
    
        client = okHttpFactory.buildDownloadOkHttpClient(
                listener = speedTestListener,
                reportInterval = OkHttpWayConst.REPORT_INTERVAL
        )
    
        val call = client.newCall(request)
        call.execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val string = response.body()?.string()
        }
    }
    
...

}
~~~

В классе OkHttpApiSpeedTestDownload напишем метод executeDownloadSpeedTest(), который будет отвечать за запуск нашего запроса, на получение файла с сервера.

А в классе OkHttpApiSpeedTestUpload напишем аналогичный метод executeUploadSpeedTest(), который будет отвечать за запуск нашего запроса, на передачу нашего файла на сервер.

~~~ kotlin
class OkHttpApiSpeedTestUpload {
...

    fun executeUploadSpeedTest(url: String, speedTestListener: SpeedTestListener, file: File) {
        client = okHttpFactory.buildUploadOkHttpClient()
    
        val requestBody = okHttpFactory.buildUploadRequestUploadBody(
                listener = speedTestListener,
                reportInterval = OkHttpWayConst.REPORT_INTERVAL,
                file = file
        )
    
        val request = okHttpFactory.buildUploadRequest(
                url = url,
                requestBody = requestBody
        )
    
        val call = client.newCall(request)
        call.execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val string = response.body()?.string()
        }
    }
    
...

}
~~~

А метод stopTask() – отменяет запрос OkHttp клиента, который выполняется.

~~~ kotlin
fun stopTask() {
    client.dispatcher().cancelAll()
}
~~~

Когда мы закончили с классами из нашего datasource пакета, перейдём к реализации наших репозиториев.

#### OkHttp. Repository

Далее мы создаём репозитории OkHttpSpeedTestDownloadRepositoryImpl и OkHttpSpeedTestUploadRepositoryImpl. Эти репозитории реализуют SpeedTestListener и создают Rx обёртку, над нашим datasource, которую мы используем в domain-слое нашего приложения. 

В конструктор репозитория OkHttpSpeedTestDownloadRepositoryImpl, мы передаём OkHttpApiSpeedTestDownload и маппер.

~~~ kotlin
class OkHttpSpeedTestDownloadRepositoryImpl {

...

    override fun measureDownloadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
        initMeasuredDownloadSpeed(speedTestEntity)
            .subscribeOn(Schedulers.io())
    
    private fun initMeasuredDownloadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
        Observable.create<FileTransferModel> {
            try {
                runDownloadSpeedTest(speedTestEntity = speedTestEntity, emitter = it)
            } catch (e: Exception) {
                it.tryOnError(e)
            }
            it.setCancellable { okHttpApiSpeedTestDownload.stopTask() }
        }.map { speedTestEntityMapper.mapToDomain(speedTestEntity, it) }
    
    private fun runDownloadSpeedTest(speedTestEntity: SpeedTestEntity, emitter: ObservableEmitter<FileTransferModel>) =
        okHttpApiSpeedTestDownload.executeDownloadSpeedTest(
            url = speedTestEntity.downloadUrl,
            speedTestListener = object : SpeedTestListener {
                override fun onComplete() {
                    emitter.onComplete()
                }
    
                override fun onNext(model: FileTransferModel) {
                    emitter.onNext(model)
                }
    
                override fun onError(throwable: Throwable) {
                    emitter.onError(throwable)
                }
            }
        )
 
...   
    
}
~~~

Рассмотрим основные методы OkHttpSpeedTestDownloadRepositoryImpl. В методе initMeasuredDownloadSpeed() мы создаём RxJava Observable и параметризуем его FileTransferModel. В методе runDownloadSpeedTest() мы оборачиваем реализацию интерфейса SpeedTestListener.

В конструктор репозитория OkHttpSpeedTestUploadRepositoryImpl, по аналогии с OkHttpSpeedTestDownloadRepositoryImpl, мы передаём OkHttpApiSpeedTestUpload, маппер и FileTools, который отвечает за создание файла-болванки, который мы будем передавать на сервер.

~~~ kotlin
class OkHttpSpeedTestUploadRepositoryImpl {

...

    override fun measureUploadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
        initMeasuredUploadSpeed(speedTestEntity)
            .subscribeOn(Schedulers.io())
    
    private fun initMeasuredUploadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
        Observable.create<FileTransferModel> {
            try {
                runUploadSpeedTest(speedTestEntity = speedTestEntity, emitter = it)
            } catch (e: Exception) {
                it.tryOnError(e)
            }
            it.setCancellable { okHttpApiSpeedTestUpload.stopTask() }
        }.map { speedTestEntityMapper.mapToDomain(speedTestEntity, it) }
    
    private fun runUploadSpeedTest(speedTestEntity: SpeedTestEntity, emitter: ObservableEmitter<FileTransferModel>) =
        okHttpApiSpeedTestUpload.executeUploadSpeedTest(
            url = speedTestEntity.uploadUrl,
            speedTestListener = object : SpeedTestListener {
                override fun onComplete() {
                    emitter.onComplete()
                }
    
                override fun onNext(model: FileTransferModel) {
                    emitter.onNext(model)
                }
    
                override fun onError(throwable: Throwable) {
                    emitter.tryOnError(throwable)
                }
            },
            file = createAndGetDummyFile()
        )
    
    private fun createAndGetDummyFile(): File =
        fileTools.createFileWithSizeByFileName(OkHttpWayConst.SPEED_TEST_FILE, OkHttpWayConst.SPEED_TEST_FILE_SIZE)
...

}
~~~

Методы OkHttpSpeedTestUploadRepositoryImpl схожи с методами класса OkHttpSpeedTestDownloadRepositoryImpl. В методе initMeasuredUploadSpeed() мы создаём RxJava Observable и параметризуем его FileTransferModel. В методе runUploadSpeedTest() мы оборачиваем реализацию интерфейса SpeedTestListener. Метод createAndGetDummyFile() возвращает файл с заданным размером, который мы будем передавать на удалённый сервер.

Реализация этим способом требует написания большого количества кода, и нетривиальной логики. Но есть другой, более простой способ.

## Измерение скорости передачи данных с помощью библиотеки JSpeedTest от Bertrand Martel

Рассмотрим второй способ с использованием библиотеки [JSpeedTest](https://github.com/bertrandmartel/speed-test-lib) от Bertrand Martel.

Для реализации спидтеста с помощью библиотеки Bertrand Martel, создадим два класса в папке datasource. Классы будут называться JSpeedTestApiDownload и JSpeedTestApiUpload. В этих классах, мы реализуем взаимодействие с библиотекой Bertrand Martel.

В конструкторы этих классов необходимо передать экземпляр класса SpeedTestSocket. Этот класс – реализация клиент-сокета. 

Инициализируем настройки экземпляра класса SpeedTestSocket: установим таймаут. Таймаут нам необходим для того, чтобы процесс измерения скорости передачи данных остановился, в случае если сервер будет отвечать слишком долго или будут проблемы с интернет-соединением. После остановки процесса спидтеста библиотека выкинет Exception.

~~~ kotlin
class JSpeedTestApiDownload {

...

    fun initDownloadSpeedtestSettings(speedtestListener: SpeedTestListener) {
        speedTestSocket = speedTestSocketFactory.build()
        speedTestSocket.socketTimeout = JSpeedTestWayConst.SOCKET_TIMEOUT
    
        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {
    
            override fun onCompletion(report: SpeedTestReport) {
                speedTestSocket.removeSpeedTestListener(this)
                speedtestListener.onNext(mapper.map(report, false, FileTransferMarker.NONE))
                speedtestListener.onComplete()
            }
    
            override fun onProgress(percent: Float, report: SpeedTestReport) {
                speedtestListener.onNext(mapper.map(report, false, FileTransferMarker.DOWNLOAD))
            }
    
            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                speedtestListener.onError(NetworkErrorException("Speed test error"))
            }
    
        })
    }

...

}
~~~

Рассмотрим метод initDownloadSpeedtestSettings(), в котором мы получаем экземпляр SpeedTestSocket из фабрики, устанавливаем таймаут и реализуем интерфейс ISpeedTestListener. В методах интерфейса ISpeedTestListener, мы с помощью SpeedTestListener передаём информацию о процессе измерения скорости передачи данных в репозиторий.

~~~ kotlin
class JSpeedTestApiUpload {
...

    fun initUploadSpeedtestSettings(speedtestListener: SpeedTestListener) {
        speedTestSocket = speedTestSocketFactory.build()
        speedTestSocket.socketTimeout = JSpeedTestWayConst.SOCKET_TIMEOUT
    
        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {
    
            override fun onCompletion(report: SpeedTestReport) {
                speedTestSocket.removeSpeedTestListener(this)
                speedtestListener.onNext(mapper.map(report, false, FileTransferMarker.NONE))
                speedtestListener.onComplete()
            }
    
            override fun onProgress(percent: Float, report: SpeedTestReport) {
                speedtestListener.onNext(mapper.map(report, false, FileTransferMarker.UPLOAD))
            }
    
            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                speedtestListener.onError(NetworkErrorException("Speed test error"))
            }
    
        })
    }

...

}
~~~

Логика метода initUploadSpeedtestSettings() в классе JSpeedTestApiUpload, аналогична логике метода initDownloadSpeedtestSettings(), за исключением того, что передаваемой модели мы ставим флаг FileTransferMarker.UPLOAD.

После инициализации, мы можем запустить процесс измерения скорости, вызвав метод startFixedDownload() для измерения скорости передачи данных с сервера и startFixedUpload() для измерения скорости передачи данных на сервер. Методы с прификсом "startFixed" будут выполняться фиксированное количество времени, после чего процесс измерения скорости передачи данных останавливается.

Информацию о количестве переданных данных мы получаем в методе onProgress реализации ISpeedTestListener и передаём эти данные в Repository.

Пример вывода текущей скорости передачи данных, в консоль.

~~~ log
08-24 09:00:34.457 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 2.936051
08-24 09:00:34.505 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 3.0615275
08-24 09:00:34.555 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 3.1870043
08-24 09:00:34.605 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 3.312481
08-24 09:00:34.655 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 3.437958
08-24 09:00:34.706 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 3.5634346
08-24 09:00:34.756 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 3.6889114
08-24 09:00:34.805 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 3.814388
08-24 09:00:34.856 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 3.939865
08-24 09:00:34.909 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 4.0653415
08-24 09:00:34.956 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 4.1908183
08-24 09:00:35.006 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 4.316295
08-24 09:00:35.056 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 4.441772
08-24 09:00:35.108 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 4.567249
08-24 09:00:35.162 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 4.6927257
08-24 09:00:35.205 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 4.8182025
08-24 09:00:35.255 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 4.943679
08-24 09:00:35.306 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 5.069156
08-24 09:00:35.356 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 5.1946325
08-24 09:00:35.405 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 5.3201094
08-24 09:00:35.455 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 5.445586
08-24 09:00:35.505 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 5.571063
08-24 09:00:35.557 7769-7920/com.sml.stp D/LoggerTimber: UPLOAD speed 5.69654
~~~

Также необходимо реализовать метод stopTask(), в котором мы будем останавливать процесс измерения скорости передачи данных и удалять все, добавленные нами листенеры:

~~~ kotlin
fun stopTask() {
    speedTestSocket.forceStopTask()
    speedTestSocket.clearListeners()
}
~~~

#### JSpeedTest. Repository

В конструктор JSpeedTestDownloadRepositoryImpl мы передаём JSpeedTestApiDownload и маппер.

~~~ kotlin
class JSpeedTestDownloadRepositoryImpl {

...

    private fun runDownloadSpeedTest(url: String): Observable<FileTransferModel> =
        Observable.create {
            jSpeedTestApiDownload.initDownloadSpeedtestSettings(
                object : SpeedTestListener {
                    override fun onComplete() {
                        it.onComplete()
                    }
    
                    override fun onNext(model: FileTransferModel) {
                        it.onNext(model)
                    }
    
                    override fun onError(throwable: Throwable) {
                        it.onError(throwable)
                    }
                }
            )
            it.setCancellable { jSpeedTestApiDownload.stopTask() }
            jSpeedTestApiDownload.runDownloadSpeedtest(url)
        }

...

}
~~~

Метод runDownloadSpeedTest() реализует интерфейс SpeedTestListener и создаётся Rx Observable параметризованный FileTransferModel. Observable оборачивает нашу реализацию SpeedTestListener.

В конструктор JSpeedTestUploadRepositoryImpl мы передаём JSpeedTestApiUpload и маппер.

~~~ kotlin
class JSpeedTestUploadRepositoryImpl {

...

    private fun runUploadSpeedTest(url: String): Observable<FileTransferModel> =
        Observable.create {
            jSpeedTestApiUpload.initUploadSpeedtestSettings(
                object : SpeedTestListener {
                    override fun onComplete() {
                        it.onComplete()
                    }
    
                    override fun onNext(model: FileTransferModel) {
                        it.onNext(model)
                    }
    
                    override fun onError(throwable: Throwable) {
                        it.onError(throwable)
                    }
                }
            )
            it.setCancellable { jSpeedTestApiUpload.stopTask() }
            jSpeedTestApiUpload.runUploadSpeedtest(url)
        }
 
...
        
}
~~~

Метод runUploadSpeedTest() реализует интерфейс SpeedTestListener и оборачивает его в Rx.

## Вывод

Как можно заметить, использование специализированной библиотеки помогает уменьшить количество кода, которое необходимо написать и делает наш код более наглядным и легким для понимания. Подробнее посмотреть код проекта можно по [ссылке на github](https://github.com/alerm-sml/speed-test-example).

Ссылки по теме:

* [JSpeedTest by Bertrand Martel](https://github.com/bertrandmartel/speed-test-lib)
* [OkHttp](https://github.com/square/okhttp)

Download

* [OkHttp recipes Download Progress](https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java)

Upload

* [Upload Progress from Stackoverflow](https://stackoverflow.com/questions/35528751/okhttp-3-tracking-multipart-upload-progress)
* [Upload Progress by Eduard Cristian Bolos](https://gist.github.com/eduardb/dd2dc530afd37108e1ac)
* [Display progress of multipart request with Retrofit and RxJava by Paulina Sadowska](https://medium.com/@PaulinaSadowska/display-progress-of-multipart-request-with-retrofit-and-rxjava-23a4a779e6ba)
