package com.example.transferdata.database.model

abstract class SensorGenericData(open var timestamp: Long){
    abstract fun printValueInCsvFormat(): String
}