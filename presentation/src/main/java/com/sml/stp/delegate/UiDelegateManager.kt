package com.sml.stp.delegate

class UiDelegateManager constructor(
        private var errorMessageDelegate: ErrorMessageDelegate,
        private var messageDelegate: MessageDelegate
) {

    fun showError(error: String) {
        errorMessageDelegate.showError(error)
    }

    fun showMessage(message: String) {
        messageDelegate.showMessage(message)
    }
}