package pro.lj.woodie02.data

import java.io.Serializable

data class Tree(
        val name: String ?= null,
        val height: String = "",
        val lifespan: String = "",
        val uses: String = "",
        val plantedOn: String = "",
        val plantedBy: String = "",
        val imageUri: String = "",
        val modelUri: String = ""
): Serializable
