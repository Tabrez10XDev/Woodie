package pro.lj.woodie02

import java.io.Serializable

data class Tree(
        val name: String = "",
        val height: String = "",
        val lifespan: String = "",
        val uses: String = "",
        val plantedOn: String = "",
        val plantedBy: String = "",
        val imageUri: String = ""
): Serializable
