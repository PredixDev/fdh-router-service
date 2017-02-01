package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author predix -
 */
public class CompareResult {

    /**
     * @param isMatch -
     * @param explanation -
     */
    CompareResult(Boolean isMatch, String explanation) {
        setIsMatch(isMatch);
        addExplanation(explanation);
    }

    private List<String> resultExplanations;
    private boolean isMatch;

    /**
     * @return -
     */
    public List<String> getResultExplanations() {
        if (this.resultExplanations == null) {
            this.resultExplanations = new ArrayList<String>();
        }
        return this.resultExplanations;
    }

    /**
     * @return -
     */
    public boolean isMatch() {
        return this.isMatch;
    }

    /**
     * @param isMatch -
     */
    public void setIsMatch(boolean isMatch) {
        this.isMatch = isMatch;
    }

    /**
     * @param explanation -
     */
    public void addExplanation(String explanation) {
        this.getResultExplanations().add(explanation);
    }

    /**
     * @param resultExplanations1 -
     */
    public void addAllExplanations(List<String> resultExplanations1) {
        this.getResultExplanations().addAll(resultExplanations1);
    }


}
