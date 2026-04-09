package com.softcode.mymagicapp.comparefeature.presentation.ui

sealed class CompareEffect {
    data class ShowMessage(val message: String) : CompareEffect()
}
