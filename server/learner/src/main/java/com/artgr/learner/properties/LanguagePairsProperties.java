package com.artgr.learner.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// app.language-pairs (application.yml) - a TEMPORARY stand-in for
// coverage-derived pairs; the real long-term constraint is translation
// coverage in the data (doc/api/swagger.yaml GET /language-pairs).
@Component
@ConfigurationProperties(prefix = "app")
public class LanguagePairsProperties {

    private List<Pair> languagePairs = new ArrayList<>();

    public List<Pair> getLanguagePairs() { return languagePairs; }
    public void setLanguagePairs(List<Pair> languagePairs) { this.languagePairs = languagePairs; }

    public static class Pair {
        private String base;
        private String target;

        public String getBase() { return base; }
        public void setBase(String base) { this.base = base; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
    }
}
