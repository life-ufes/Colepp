package com.example.colepp.database.model

abstract class SensorGenericData(open var timestamp: Long){
    abstract fun printValueInCsvFormat(): String
}