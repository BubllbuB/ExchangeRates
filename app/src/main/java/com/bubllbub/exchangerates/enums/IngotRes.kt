package com.bubllbub.exchangerates.enums

import com.bubllbub.exchangerates.R

enum class IngotRes {
    Gold {
        override fun getSymbolRes(): Int = R.mipmap.ic_ingot_gold
    },
    Silver {
        override fun getSymbolRes(): Int = R.mipmap.ic_ingot_silver
    },
    Platinum {
        override fun getSymbolRes(): Int = R.mipmap.ic_ingot_platinum
    };

    abstract fun getSymbolRes(): Int
}