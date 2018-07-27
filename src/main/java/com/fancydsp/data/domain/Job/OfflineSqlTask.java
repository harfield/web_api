package com.fancydsp.data.domain.Job;

import java.util.List;

public class OfflineSqlTask {
    private long id;
    private String name;
    private String script;
    private String fields;
    private List<Rule> rules;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public static class Rule {
        //    id,sql_placeholder,replace_value,is_optional,param_type
        private long id;
        private String sqlPlaceholder;
        private String replaceValue;
        private boolean isOptional;
        private int paramType;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getSqlPlaceholder() {
            return sqlPlaceholder;
        }

        public void setSqlPlaceholder(String sqlPlaceholder) {
            this.sqlPlaceholder = sqlPlaceholder;
        }

        public String getReplaceValue() {
            return replaceValue;
        }

        public void setReplaceValue(String replaceValue) {
            this.replaceValue = replaceValue;
        }

        public boolean getIsOptional() {
            return isOptional;
        }

        public void setIsOptional(boolean optional) {
            isOptional = optional;
        }

        public int getParamType() {
            return paramType;
        }

        public void setParamType(int paramType) {
            this.paramType = paramType;
        }
    }
}
