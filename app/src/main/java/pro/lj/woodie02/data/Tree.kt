package pro.lj.woodie02.data

import java.io.Serializable

data class Tree(
        val name: String ?= null,
        val height: String = "",
        val lifespan: String = "",
        val uses: String = "",
        val imageUri: String = "",
        val scientificName: String = "",
        val vernacularNames: String = "",
        val modelUri: String = ""
): Serializable
