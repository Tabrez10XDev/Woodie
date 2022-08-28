package pro.lj.woodie02.data

import java.io.Serializable

data class Tree(
        val name: String ?= null,
        val botanicalDes : String = "",
        val commonName: String = "",
        val family: String = "",
        val flowers: String = "",
        val fruits: String = "",
        val habit: String = "",
        val imageUri: List<String> ?= null,
        val leaves: String = "",
        val origin: String = "",
        val uses: String = "",
        val tamilName: String = "",
        val scientificName : String = "",
        val modelUri: String = ""
): Serializable
