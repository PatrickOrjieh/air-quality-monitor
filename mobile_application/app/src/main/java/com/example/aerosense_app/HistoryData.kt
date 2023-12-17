package com.example.aerosense_app

data class HistoryData (

    var overall_avg_pm2_5: Double,
    var overall_avg_score: Double,
    var overall_avg_voc: Double,
    var weekly_scores: List<WeeklyScore>,

)

data class WeeklyScore(
    val airQualityScore: Double,
    val day: String
)
