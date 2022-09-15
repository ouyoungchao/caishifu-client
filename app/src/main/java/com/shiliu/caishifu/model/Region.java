package com.shiliu.caishifu.model;


import com.orm.SugarRecord;


public class Region extends SugarRecord {

    private String parentId;

    /**
     *哪一类区域
     */
    private String level;

    /**
     * 名称
     */
    private String name;

    /**
     * 国家码
     */
    private String code;

    /**
     * 排序
     */
    private Float seq;


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getSeq() {
        return seq;
    }

    public void setSeq(Float seq) {
        this.seq = seq;
    }
}
