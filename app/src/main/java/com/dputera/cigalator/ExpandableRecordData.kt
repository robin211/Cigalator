package com.dputera.cigalator

class ExpandableRecordData {
    companion object{
        const val PARENT = 1
        const val CHILD = 2
        const val GRANDCHILD = 3
    }
    lateinit var recordParent : RecordData.YearData
    lateinit var recordChild : RecordData.YearData.Monthdata
    lateinit var recordGrandChild : RecordData.YearData.Monthdata.TimeData
    var type : Int
    var isExpanded : Boolean
    var isCloseShown : Boolean

    constructor(type: Int, recordParent : RecordData.YearData, isExpanded : Boolean = false, isCloseShown : Boolean = true){
        this.type = type
        this.recordParent = recordParent
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    constructor(type: Int, recordChild : RecordData.YearData.Monthdata, isExpanded : Boolean = false, isCloseShown : Boolean = true){
        this.type = type
        this.recordChild = recordChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    constructor(type: Int, recordGrandChild : RecordData.YearData.Monthdata.TimeData, isExpanded : Boolean = false, isCloseShown : Boolean = false){
        this.type = type
        this.recordGrandChild = recordGrandChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }
}