package com.augmentingtechs.vitalsigns

import android.os.Environment
import java.io.File

class Constants {

    val singleAdInterval = 2
    val allAdInterval = 3
    val rewardCounter = 5

    val contentDIR = Environment.getExternalStorageDirectory().toString() + File.separator + ".VitalSigns"
    val contentNAME = "vs_data"
}