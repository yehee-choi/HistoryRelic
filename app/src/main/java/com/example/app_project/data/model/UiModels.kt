package com.example.app_project.data.model

import java.util.*

// 대시보드용 간단한 유물 클래스 (사진, 이름, 시기만)
data class DashboardArtifact(
    val id: String,
    val imgUri: String?,
    val nameKr: String,
    val era: String? = null  // 시기 정보
)

// 유물 상세 정보
data class ArtifactDetail(
    val id: String,
    val nameKr: String,
    val description: String,
    val imageList: List<String>, // 여러 이미지 URL들
    val materialName: String,
    val museumName: String,
    val nationalityName: String,
    val purposeName: String,
    val relatedArtifact: RelatedArtifactInfo? = null
)

data class RelatedArtifactInfo(
    val id: String,
    val name: String,
    val museumName: String
)

// OCR 결과 데이터 클래스
data class OcrResult(
    val id: String,
    val text: String,
    val confidence: Float,
    val language: String,
    val processingTime: Long,
    val timestamp: Date,
    val imageUri: String,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

// ===== 확장 함수들 (API 모델을 UI 모델로 변환) =====

// BriefInfo를 대시보드용으로 변환
fun BriefInfo.toDashboardArtifact(): DashboardArtifact {
    return DashboardArtifact(
        id = id,
        imgUri = imgThumUriM, // 중간 크기 썸네일 사용
        nameKr = nameKr,
        era = nationalityName // 시기 정보
    )
}

// SearchApiResponse를 대시보드용 리스트로 변환
fun SearchApiResponse.toDashboardArtifactList(): List<DashboardArtifact> {
    return if (success) {
        data.brief_info_list.map { it.toDashboardArtifact() }
    } else {
        emptyList()
    }
}

// DetailApiResponse를 ArtifactDetail로 변환
fun DetailApiResponse.toArtifactDetail(): ArtifactDetail? {
    return if (success && data.detail_info_list.isNotEmpty()) {
        val detailInfo = data.detail_info_list[0]
        detailInfo.toArtifactDetail()
    } else {
        null
    }
}
//NullSatety
// DetailInfoItem을 ArtifactDetail로 변환
fun DetailInfoItem.toArtifactDetail(): ArtifactDetail {
    return ArtifactDetail(
        id = item.id,
        nameKr = item.nameKr,
        description = item.desc,
        imageList = imageList.map { it.imgUri },
        materialName = item.materialName,
        museumName = item.museumName2,
        nationalityName = "${item.nationalityName1} ${item.nationalityName2}".trim(),
        purposeName = item.purposeName,
        relatedArtifact = related?.let { // null 체크 추가
            RelatedArtifactInfo(
                id = it.reltId,
                name = it.reltRelicName,
                museumName = it.reltMuseumFullName
            )
        } // related가 null이면 relatedArtifact도 null
    )
}