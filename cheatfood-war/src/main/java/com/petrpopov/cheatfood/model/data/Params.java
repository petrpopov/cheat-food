package com.petrpopov.cheatfood.model.data;

import com.petrpopov.cheatfood.model.entity.Type;

import java.util.List;

/**
 * User: petrpopov
 * Date: 19.08.13
 * Time: 11:10
 */
public class Params {

    private List<Type> types;
    private Double maxPrice;
    private Double recommendedPrice;
    private Integer commentSecondsDelay;

    public Params() {
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(Double recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public Integer getCommentSecondsDelay() {
        return commentSecondsDelay;
    }

    public void setCommentSecondsDelay(Integer commentSecondsDelay) {
        this.commentSecondsDelay = commentSecondsDelay;
    }
}
