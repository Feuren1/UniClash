package project.main.uniclash.newBuildingLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import project.main.uniclash.BuildingType

/*
Saves user input, in case of user has to leave the activity, he had not to reenter the information.
Singleton, are not good praxis for this => bad controlling (life time controlling)
 */
class NewBuildingSingleTon private constructor() {
    private var title by mutableStateOf("")
    private var description by mutableStateOf("")
    private var building by mutableStateOf(BuildingType.ARENA)
    companion object {
        val instance:NewBuildingSingleTon by lazy {
            NewBuildingSingleTon()
        }
    }

    @JvmName("getTitleImpl")
    fun getTitle(): String {
        return title
    }
    @JvmName("setTitleImpl")
    fun setTitle(title : String){
        this.title = title
    }
    @JvmName("getDescriptionImpl")
    fun getDescription(): String{
        return description
    }
    @JvmName("setDescriptionImpl")
    fun setDescription(description :String){
        this.description = description
    }
    @JvmName("getBuildingImpl")
    fun getBuilding():BuildingType{
        return building
    }
    @JvmName("setBuildingImpl")
    fun setBuilding(building : BuildingType){
        this.building = building
    }
}