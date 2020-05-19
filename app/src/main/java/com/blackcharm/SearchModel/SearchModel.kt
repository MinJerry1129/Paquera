package com.blackcharm.SearchModel

class SearchModel : Comparable<SearchModel> {
    override fun compareTo(other: SearchModel): Int {
        if (priority > other.priority) {
            return -1
        } else if (priority < other.priority) {
            return 1
        }

        return 0
    }

    var id:String? = null
    var key:String? = null
    var name:String? = null
    var age:String? = null
    var occupation:String? = null
    var loc:String? = null
    var profileImageUrl:String? = null
    var priority: Long = 99999999999999
    var yourSex:String? = null
    var aboutMe:String? = null
    var email:String? = null
    var meetChecked:Boolean? = false
    var discoverChecked:Boolean? = false
    var checkedByAdmin:Boolean? = false
    var searchDateChecked:Boolean? = false
    var searchMeetChecked:Boolean? = false
    var travelChecked:Boolean? = false
    var acceptingGuests:String? = null
    var dateChecked:Boolean? = false
}