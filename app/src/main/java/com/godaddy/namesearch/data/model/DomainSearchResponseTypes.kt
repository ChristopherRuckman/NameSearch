package com.godaddy.namesearch

import com.google.gson.annotations.SerializedName

data class DomainSearchProductResponse(
    @SerializedName("PriceInfo") val priceInfo: PriceInfo,
    @SerializedName("Content") val content: Content?,
    @SerializedName("ProductId") val productId: Int
)

data class PriceInfo(
    @SerializedName("CurrentPriceDisplay") val currentPriceDisplay: String,
    @SerializedName("ListPriceDisplay") val listPriceDisplay: String,
    @SerializedName("PromoRegLengthFlag") val promoLength: Int,
)

data class Content (
    val header: String?,
    val messages: String?,
    val phases: List<String>?,
    val subHeader: String?,
    @SerializedName("TLD") val tld: String
)
data class DomainSearchRecommendedResponse(
    @SerializedName("Products") val products: List<DomainSearchProductResponse>,
    @SerializedName("RecommendedDomains") val domains: List<RecommendedDomain>
)

data class RecommendedDomain(
    @SerializedName("Fqdn") val fqdn: String,
    @SerializedName("Extension") val tld: String,
    val tierId: Int,
    @SerializedName("IsPremiumTier") val isPremium: Boolean,
    @SerializedName("ProductId") val productId: Int,
    val inventory: String
)
data class DomainSearchExactMatchResponse (
    @SerializedName("Products") val products: List<DomainSearchProductResponse>,
    @SerializedName("ExactMatchDomain") val domain: ExactMatch
)

data class ExactMatch (
    @SerializedName("Fqdn") val fqdn: String,
    @SerializedName("Extension") val tld: String,
    val tierId: Int,
    val isAvailable: Boolean,
    @SerializedName("IsPremiumTier") val isPremium: Boolean,
    @SerializedName("ProductId") val productId: Int
)
