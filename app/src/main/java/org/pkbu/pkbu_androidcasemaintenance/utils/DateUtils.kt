package org.pkbu.pkbu_androidcasemaintenance.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    var strDatePattern = "MM/dd/yy"
    var dateFormat = SimpleDateFormat(strDatePattern, Locale.US)
}