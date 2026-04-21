package com.example.userservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maps to the us_fwd.adm_usr table.
 * Composite PK: (co_cd, usr_id)
 */
public class AdmUsr {

    private String usrId;
    private String usrNm;
    private String usrPwd;
    private String comUsrDt;
    private String comUsrSx;
    private String coTelNo;
    private String hmTelNo;
    private String mphnNo;
    private String faxNo;
    private String usrEml;
    private String actFlg;
    private String deltStsFlg;
    private String creUsrId;
    private LocalDateTime creDt;
    private String updUsrId;
    private LocalDateTime updDt;
    private String imgUrl;
    private Integer age;
    private String ntlt;
    private String mrrStsCd;
    private String edu;
    private String hby;
    private String wrkExp;
    private String hmAddr;
    private String spct;
    private String engLvl;
    private String salyLvl;
    private String prjHis;
    private String cntCd;
    private String ctyNm;
    private String isRoot;
    private String brdyVal;
    private String coCd;
    private BigDecimal ctrbPntNo;
    private BigDecimal perfPntNo;
    private BigDecimal lstPerfPntNo;
    private BigDecimal lstCtrbPntNo;
    private String empeNo;
    private LocalDateTime empeStDt;
    private String addPrbtnVacFlg;
    private String locCd;
    private String empeTpCd;
    private String fullNm;
    private String usrPwdTemp;
    private String dvcTknCd;
    private String faceUsrId;
    private String skyId;
    private String ofcCd;
    private LocalDate empeEndDt;
    private String snsId;
    private String snsNm;
    private String ctyCd;
    private String usrEmlPwd;
    private String emlCtnt;
    private String emlTknVal;

    public AdmUsr() {
    }

    // --- Getters & Setters ---

    public String getUsrId() { return usrId; }
    public void setUsrId(String usrId) { this.usrId = usrId; }

    public String getUsrNm() { return usrNm; }
    public void setUsrNm(String usrNm) { this.usrNm = usrNm; }

    public String getUsrPwd() { return usrPwd; }
    public void setUsrPwd(String usrPwd) { this.usrPwd = usrPwd; }

    public String getComUsrDt() { return comUsrDt; }
    public void setComUsrDt(String comUsrDt) { this.comUsrDt = comUsrDt; }

    public String getComUsrSx() { return comUsrSx; }
    public void setComUsrSx(String comUsrSx) { this.comUsrSx = comUsrSx; }

    public String getCoTelNo() { return coTelNo; }
    public void setCoTelNo(String coTelNo) { this.coTelNo = coTelNo; }

    public String getHmTelNo() { return hmTelNo; }
    public void setHmTelNo(String hmTelNo) { this.hmTelNo = hmTelNo; }

    public String getMphnNo() { return mphnNo; }
    public void setMphnNo(String mphnNo) { this.mphnNo = mphnNo; }

    public String getFaxNo() { return faxNo; }
    public void setFaxNo(String faxNo) { this.faxNo = faxNo; }

    public String getUsrEml() { return usrEml; }
    public void setUsrEml(String usrEml) { this.usrEml = usrEml; }

    public String getActFlg() { return actFlg; }
    public void setActFlg(String actFlg) { this.actFlg = actFlg; }

    public String getDeltStsFlg() { return deltStsFlg; }
    public void setDeltStsFlg(String deltStsFlg) { this.deltStsFlg = deltStsFlg; }

    public String getCreUsrId() { return creUsrId; }
    public void setCreUsrId(String creUsrId) { this.creUsrId = creUsrId; }

    public LocalDateTime getCreDt() { return creDt; }
    public void setCreDt(LocalDateTime creDt) { this.creDt = creDt; }

    public String getUpdUsrId() { return updUsrId; }
    public void setUpdUsrId(String updUsrId) { this.updUsrId = updUsrId; }

    public LocalDateTime getUpdDt() { return updDt; }
    public void setUpdDt(LocalDateTime updDt) { this.updDt = updDt; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getNtlt() { return ntlt; }
    public void setNtlt(String ntlt) { this.ntlt = ntlt; }

    public String getMrrStsCd() { return mrrStsCd; }
    public void setMrrStsCd(String mrrStsCd) { this.mrrStsCd = mrrStsCd; }

    public String getEdu() { return edu; }
    public void setEdu(String edu) { this.edu = edu; }

    public String getHby() { return hby; }
    public void setHby(String hby) { this.hby = hby; }

    public String getWrkExp() { return wrkExp; }
    public void setWrkExp(String wrkExp) { this.wrkExp = wrkExp; }

    public String getHmAddr() { return hmAddr; }
    public void setHmAddr(String hmAddr) { this.hmAddr = hmAddr; }

    public String getSpct() { return spct; }
    public void setSpct(String spct) { this.spct = spct; }

    public String getEngLvl() { return engLvl; }
    public void setEngLvl(String engLvl) { this.engLvl = engLvl; }

    public String getSalyLvl() { return salyLvl; }
    public void setSalyLvl(String salyLvl) { this.salyLvl = salyLvl; }

    public String getPrjHis() { return prjHis; }
    public void setPrjHis(String prjHis) { this.prjHis = prjHis; }

    public String getCntCd() { return cntCd; }
    public void setCntCd(String cntCd) { this.cntCd = cntCd; }

    public String getCtyNm() { return ctyNm; }
    public void setCtyNm(String ctyNm) { this.ctyNm = ctyNm; }

    public String getIsRoot() { return isRoot; }
    public void setIsRoot(String isRoot) { this.isRoot = isRoot; }

    public String getBrdyVal() { return brdyVal; }
    public void setBrdyVal(String brdyVal) { this.brdyVal = brdyVal; }

    public String getCoCd() { return coCd; }
    public void setCoCd(String coCd) { this.coCd = coCd; }

    public BigDecimal getCtrbPntNo() { return ctrbPntNo; }
    public void setCtrbPntNo(BigDecimal ctrbPntNo) { this.ctrbPntNo = ctrbPntNo; }

    public BigDecimal getPerfPntNo() { return perfPntNo; }
    public void setPerfPntNo(BigDecimal perfPntNo) { this.perfPntNo = perfPntNo; }

    public BigDecimal getLstPerfPntNo() { return lstPerfPntNo; }
    public void setLstPerfPntNo(BigDecimal lstPerfPntNo) { this.lstPerfPntNo = lstPerfPntNo; }

    public BigDecimal getLstCtrbPntNo() { return lstCtrbPntNo; }
    public void setLstCtrbPntNo(BigDecimal lstCtrbPntNo) { this.lstCtrbPntNo = lstCtrbPntNo; }

    public String getEmpeNo() { return empeNo; }
    public void setEmpeNo(String empeNo) { this.empeNo = empeNo; }

    public LocalDateTime getEmpeStDt() { return empeStDt; }
    public void setEmpeStDt(LocalDateTime empeStDt) { this.empeStDt = empeStDt; }

    public String getAddPrbtnVacFlg() { return addPrbtnVacFlg; }
    public void setAddPrbtnVacFlg(String addPrbtnVacFlg) { this.addPrbtnVacFlg = addPrbtnVacFlg; }

    public String getLocCd() { return locCd; }
    public void setLocCd(String locCd) { this.locCd = locCd; }

    public String getEmpeTpCd() { return empeTpCd; }
    public void setEmpeTpCd(String empeTpCd) { this.empeTpCd = empeTpCd; }

    public String getFullNm() { return fullNm; }
    public void setFullNm(String fullNm) { this.fullNm = fullNm; }

    public String getUsrPwdTemp() { return usrPwdTemp; }
    public void setUsrPwdTemp(String usrPwdTemp) { this.usrPwdTemp = usrPwdTemp; }

    public String getDvcTknCd() { return dvcTknCd; }
    public void setDvcTknCd(String dvcTknCd) { this.dvcTknCd = dvcTknCd; }

    public String getFaceUsrId() { return faceUsrId; }
    public void setFaceUsrId(String faceUsrId) { this.faceUsrId = faceUsrId; }

    public String getSkyId() { return skyId; }
    public void setSkyId(String skyId) { this.skyId = skyId; }

    public String getOfcCd() { return ofcCd; }
    public void setOfcCd(String ofcCd) { this.ofcCd = ofcCd; }

    public LocalDate getEmpeEndDt() { return empeEndDt; }
    public void setEmpeEndDt(LocalDate empeEndDt) { this.empeEndDt = empeEndDt; }

    public String getSnsId() { return snsId; }
    public void setSnsId(String snsId) { this.snsId = snsId; }

    public String getSnsNm() { return snsNm; }
    public void setSnsNm(String snsNm) { this.snsNm = snsNm; }

    public String getCtyCd() { return ctyCd; }
    public void setCtyCd(String ctyCd) { this.ctyCd = ctyCd; }

    public String getUsrEmlPwd() { return usrEmlPwd; }
    public void setUsrEmlPwd(String usrEmlPwd) { this.usrEmlPwd = usrEmlPwd; }

    public String getEmlCtnt() { return emlCtnt; }
    public void setEmlCtnt(String emlCtnt) { this.emlCtnt = emlCtnt; }

    public String getEmlTknVal() { return emlTknVal; }
    public void setEmlTknVal(String emlTknVal) { this.emlTknVal = emlTknVal; }
}
