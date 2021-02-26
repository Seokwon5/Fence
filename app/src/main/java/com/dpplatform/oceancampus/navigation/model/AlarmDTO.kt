package com.dpplatform.oceancampus.navigation.model

data class AlarmDTO(
        var destinationUid : String? = null,
        var userId : String? = null,
        var uid : String? = null,
        var kind : Int? = null,
        var timestamp : Long? = null,
        var message : String? = null
)