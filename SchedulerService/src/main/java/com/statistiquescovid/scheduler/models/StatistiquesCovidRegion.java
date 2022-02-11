package com.statistiquescovid.scheduler.models;

import java.util.Date;

public class StatistiquesCovidRegion {

	private Date date; // "2021-11-10"
	private String dep; // 9, 2A, 2B ...
	private String reg; // 76
	private String lib_dep; // "Ariège"
	private String lib_reg; // "Occitanie"

	private Double tx_pos; // Taux de positivité des tests virologiques
	private Double tx_incid; // Taux d'incidence
	private Double TO; // Taux d'occupation : tension hospitalière sur la capacité en réanimation
	private Double R; // Facteur de reproduction du virus
	
	private Long hosp; // Nombre de patients actuellement hospitalisés pour COVID-19
	private Long rea; // Nombre de patients actuellement en réanimation ou en soins intensifs
	private Long rad; // Nombre cumulé de patients ayant été hospitalisés pour COVID-19 et de retour à domicile en raison de l'amélioration de leur état de santé
	private Long dchosp; // Décès à l'hôpital
	private Long reg_rea;
	
	private Long incid_hosp; // Nouveaux patients décédés à l’hôpital au cours des dernières 24h
	private Long incid_rea; // Nombre de nouveaux patients admis en réanimation au cours des dernières 24h
	private Long incid_rad; // Nouveaux retours à domicile au cours des dernières 24h
	private Long incid_dchosp; // Nouveaux patients décédés à l’hôpital au cours des dernières 24h
	
	private Long reg_incid_rea;
	private Long pos; // Nombre de personnes déclarées positives (J-3 date de prélèvement)
	private Long pos_7j; // Nombre de personnes déclarées positives sur une semaine (J-3 date de prélèvement)
	private Float cv_dose1; // 85.6

	private Long esms_dc; // Décès en ESMS
	private Long esms_cas; // Cas confirmés en ESMS

	public StatistiquesCovidRegion() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getDep() {
		return dep;
	}
	public void setDep(String dep) {
		this.dep = dep;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getReg() {
		return reg;
	}
	public void setReg(String reg) {
		this.reg = reg;
	}
	public String getLib_dep() {
		return lib_dep;
	}
	public void setLib_dep(String lib_dep) {
		this.lib_dep = lib_dep;
	}
	public String getLib_reg() {
		return lib_reg;
	}
	public void setLib_reg(String lib_reg) {
		this.lib_reg = lib_reg;
	}
	public Double getTx_pos() {
		return tx_pos;
	}
	public void setTx_pos(Double tx_pos) {
		this.tx_pos = tx_pos;
	}
	public Double getTx_incid() {
		return tx_incid;
	}
	public void setTx_incid(Double tx_incid) {
		this.tx_incid = tx_incid;
	}
	public Double getTO() {
		return TO;
	}
	public void setTO(Double tO) {
		TO = tO;
	}
	public Double getR() {
		return R;
	}
	public void setR(Double r) {
		R = r;
	}
	public Long getHosp() {
		return hosp;
	}
	public void setHosp(Long hosp) {
		this.hosp = hosp;
	}
	public Long getRea() {
		return rea;
	}
	public void setRea(Long rea) {
		this.rea = rea;
	}
	public Long getRad() {
		return rad;
	}
	public void setRad(Long rad) {
		this.rad = rad;
	}
	public Long getDchosp() {
		return dchosp;
	}
	public void setDchosp(Long dchosp) {
		this.dchosp = dchosp;
	}
	public Long getReg_rea() {
		return reg_rea;
	}
	public void setReg_rea(Long reg_rea) {
		this.reg_rea = reg_rea;
	}
	public Long getIncid_hosp() {
		return incid_hosp;
	}
	public void setIncid_hosp(Long incid_hosp) {
		this.incid_hosp = incid_hosp;
	}
	public Long getIncid_rea() {
		return incid_rea;
	}
	public void setIncid_rea(Long incid_rea) {
		this.incid_rea = incid_rea;
	}
	public Long getIncid_rad() {
		return incid_rad;
	}
	public void setIncid_rad(Long incid_rad) {
		this.incid_rad = incid_rad;
	}
	public Long getIncid_dchosp() {
		return incid_dchosp;
	}
	public void setIncid_dchosp(Long incid_dchosp) {
		this.incid_dchosp = incid_dchosp;
	}
	public Long getReg_incid_rea() {
		return reg_incid_rea;
	}
	public void setReg_incid_rea(Long reg_incid_rea) {
		this.reg_incid_rea = reg_incid_rea;
	}
	public Long getPos() {
		return pos;
	}
	public void setPos(Long pos) {
		this.pos = pos;
	}
	public Long getPos_7j() {
		return pos_7j;
	}
	public void setPos_7j(Long pos_7j) {
		this.pos_7j = pos_7j;
	}
	public Float getCv_dose1() {
		return cv_dose1;
	}
	public void setCv_dose1(Float cv_dose1) {
		this.cv_dose1 = cv_dose1;
	}
	public Long getEsms_dc() {
		return esms_dc;
	}
	public void setEsms_dc(Long esms_dc) {
		this.esms_dc = esms_dc;
	}
	public Long getEsms_cas() {
		return esms_cas;
	}
	public void setEsms_cas(Long esms_cas) {
		this.esms_cas = esms_cas;
	}
	@Override
	public String toString() {
		return "StatistiquesCovidRegion [dep=" + dep + ", date=" + date + ", reg=" + reg + ", lib_dep=" + lib_dep
				+ ", lib_reg=" + lib_reg + ", tx_pos=" + tx_pos + ", tx_incid=" + tx_incid + ", TO=" + TO + ", R=" + R
				+ ", hosp=" + hosp + ", rea=" + rea + ", rad=" + rad + ", dchosp=" + dchosp + ", reg_rea=" + reg_rea
				+ ", incid_hosp=" + incid_hosp + ", incid_rea=" + incid_rea + ", incid_rad=" + incid_rad
				+ ", incid_dchosp=" + incid_dchosp + ", reg_incid_rea=" + reg_incid_rea + ", pos=" + pos + ", pos_7j="
				+ pos_7j + ", cv_dose1=" + cv_dose1 + ", esms_dc=" + esms_dc + ", esms_cas=" + esms_cas + "]";
	}

}
