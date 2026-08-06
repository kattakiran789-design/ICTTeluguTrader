package com.icttelugu.trader.model

data class AnalysisResponse(
    val marketStructure: String = "",
    val keyLevels: String = "",
    val tradeSetup: String = "",
    val entryPrice: String = "",
    val stopLoss: String = "",
    val target: String = "",
    val riskReward: String = "",
    val confidenceScore: String = "", // High, Medium, Low
    val fullTeluguAnalysis: String = ""
)
