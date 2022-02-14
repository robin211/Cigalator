package com.dputera.cigalator

data class RecordData(val years: List<YearData>) {
    data class YearData(
        val year: Int,
        val months: List<Monthdata>
    ) {
        data class Monthdata(
            val month: String,
            val cigTimes: List<TimeData>
        ) {
            data class TimeData(
                val time: String,
                val isMood: Boolean,
                val reason: String
            )
        }
    }
}