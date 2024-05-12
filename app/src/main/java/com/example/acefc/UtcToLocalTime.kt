package com.example.acefc

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun convertUtcToLocalTime(utcTime: String): String {
    // Define input and output date formats
    val inputFormat = SimpleDateFormat("HH:mm")
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("HH:mm")
    outputFormat.timeZone = TimeZone.getDefault() // Use local timezone

    // Parse UTC time string into a Date object
    val utcDate: Date = inputFormat.parse(utcTime) ?: return ""

    // Format the local time in HH:mm format
    return outputFormat.format(utcDate)
}