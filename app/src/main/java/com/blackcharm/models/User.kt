package com.blackcharm.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val name: String, val username: String, val email: String, val profileImageUrl: String, val age: String, val occupation: String, val loc: String, val currentLoc: String, val dateChecked: Boolean, val meetChecked: Boolean, val acceptingGuests: String, val aboutMe: String, val aboutMyPlace: String, val searchLoc: String, val yourSex: String, val travelChecked: Boolean, val fromDevice: String?, val aboutServicesEnabled: Boolean, val offerServiseChecked: Boolean, val aboutServices: String, val disconectTime: Long, val referalMethod: String?,val membershipdate: String?, val membershipstatus: String?,val referalCode: String?, val referalName: String?, val referalCompany: String?): Parcelable {
    constructor() : this("", "", "","", "", "", "", "", false, false, "","", "", "", "", false, "", false, false, "", -99999999999999, "", "", "", "","","")
}
