package com.siberika.idea.pascal.sdk

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import com.siberika.idea.pascal.jps.sdk.PascalCompilerFamily

class TurboSdkType : BasePascalSdkType("TurboSdkType", PascalCompilerFamily.TURBO) {

    init {
        loadResources("turbo")
    }

    override fun getPresentableName(): String = "Turbo Pascal SDK"

    override fun isValidSdkHome(path: String?): Boolean = true

    override fun suggestSdkName(currentSdkName: String?, sdkHome: String?): String = "Turbo Pascal 7.0"

    override fun suggestHomePath(): String? = null

    override fun getVersionString(sdk: Sdk): String? = "7.0"

    override fun setupSdkPaths(sdk: Sdk) {
        println(sdk.homeDirectory)
        println(sdk.homePath)
        val dir = sdk.homeDirectory ?: return

        val modificator = sdk.sdkModificator

        modificator.addRoot(dir, OrderRootType.CLASSES)
        modificator.commitChanges()
    }

    override fun isRootTypeApplicable(type: OrderRootType): Boolean =
            type == OrderRootType.SOURCES || type == OrderRootType.CLASSES

    override fun createAdditionalDataConfigurable(sdkModel: SdkModel, sdkModificator: SdkModificator): AdditionalDataConfigurable? =
            PascalSdkConfigUI()

    companion object {

        @JvmStatic
        fun getInstance(): TurboSdkType = SdkType.findInstance(TurboSdkType::class.java)

    }
}