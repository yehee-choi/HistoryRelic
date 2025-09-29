package com.example.app_project.data.model

// 검색 API 응답
data class SearchApiResponse(
    val data: SearchData,
    val message: String,
    val success: Boolean,
    val userId: Int
)

data class SearchData(
    val brief_info_list: List<BriefInfo>,
    val totalCount: Int
)

// 유물 간략 정보 (서버 응답)
data class BriefInfo(
    val id: String,
    val imgThumUriL: String,
    val imgThumUriM: String,
    val imgThumUriS: String,
    val imgUri: String,
    val materialName: String?,
    val museumName1: String,
    val museumName2: String,
    val museumName3: String,
    val nameKr: String,
    val nationalityName: String?,
    val purposeName: String?
)

// 상세정보 API 응답
data class DetailApiResponse(
    val data: DetailData,
    val message: String,
    val success: Boolean,
    val userId: Int
)

data class DetailData(
    val detail_info_list: List<DetailInfoItem>
)

data class DetailInfoItem(
    val imageList: List<DetailImageInfo>,
    val item: DetailItem,
    val related: RelatedInfo
)

data class DetailImageInfo(
    val imgThumUriL: String,
    val imgThumUriM: String,
    val imgThumUriS: String,
    val imgUri: String
)

data class DetailItem(
    val desc: String,
    val id: String,
    val imgThumUriL: String,
    val imgThumUriM: String,
    val imgThumUriS: String,
    val imgUri: String,
    val materialName: String,
    val museumName1: String,
    val museumName2: String,
    val nameKr: String,
    val nationalityName1: String,
    val nationalityName2: String,
    val purposeName: String
)

data class RelatedInfo(
    val reltId: String,
    val reltMuseumFullName: String,
    val reltRelicName: String
)