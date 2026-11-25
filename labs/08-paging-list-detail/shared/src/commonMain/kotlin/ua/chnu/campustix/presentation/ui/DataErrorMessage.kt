package ua.chnu.campustix.presentation.ui

import org.jetbrains.compose.resources.StringResource
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.resources.Res
import ua.chnu.campustix.resources.error_no_internet
import ua.chnu.campustix.resources.error_server
import ua.chnu.campustix.resources.error_unauthorized
import ua.chnu.campustix.resources.error_unknown

fun DataError.messageRes(): StringResource = when (this) {
    DataError.Unauthorized -> Res.string.error_unauthorized
    DataError.NoInternet -> Res.string.error_no_internet
    DataError.Server -> Res.string.error_server
    DataError.Serialization, DataError.Unknown -> Res.string.error_unknown
}
