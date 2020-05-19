package com.blackcharm.SearchListener

import com.blackcharm.SearchModel.SearchModel

interface IFirebaseLoadDone {
    fun onSearchResaultSuccess(profileList:List<SearchModel>, dataid: List<String>)
    fun onSearchResaultFailed(message:String)
    fun OnSearchPermittedUserSuccess(profileList:List<String>, flag : Int)
}