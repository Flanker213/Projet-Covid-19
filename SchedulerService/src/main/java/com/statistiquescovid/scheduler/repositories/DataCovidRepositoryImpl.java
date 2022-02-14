package com.statistiquescovid.scheduler.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.statistiquescovid.scheduler.entities.StatistiquesCovidDepartement;
import com.statistiquescovid.scheduler.models.StatistiquesCovidRegion;
import com.statistiquescovid.scheduler.utils.DateUtils;

@Repository
public class DataCovidRepositoryImpl implements DataCovidRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	public static int DEFAULT_TOP_N = 20;

	@Override
	public Page<StatistiquesCovidDepartement> search(String departementId, String regionId, Date dateDebut, Date dateFin, String trie, Pageable pageable) {
		// TODO Auto-generated method stub
		Criteria criteria = new Criteria();
		if (dateDebut != null && dateFin != null) {
			criteria.andOperator(Criteria.where("date").gte(DateUtils.getFormattedFromDateTime(dateDebut)),
					Criteria.where("date").lte(DateUtils.getFormattedToDateTime(dateFin)));
		}
		else if(dateDebut != null) {
			criteria.and("date").gte(DateUtils.getFormattedFromDateTime(dateDebut));
		} 
		else if(dateFin != null) {
			criteria.and("date").lte(DateUtils.getFormattedToDateTime(dateFin));
		}

		if(departementId != null && !departementId.trim().isEmpty()) {
			criteria.and("dep").is(departementId);
		}

		if(regionId != null && !regionId.trim().isEmpty()) {
			criteria.and("reg").is(regionId);
		}

		Pageable pageableAb = pageable != null ? pageable : PageRequest.of(0, 10);
		Query query = new Query().with(pageableAb);
		query.addCriteria(criteria);
		// TODO : Appliquer l'ordre de trie spécifié
		Sort sortOperation = Sort.by("rad").ascending(); // 'CC' par défaut !
		// Traiter le cas où l'unité de temps est précisé !
		if (trie != null && trie.equalsIgnoreCase("CD")) {
			sortOperation = Sort.by("rad").descending();
		}
		else if (trie != null && trie.equalsIgnoreCase("DC")) { // Décès à l'hopital
			sortOperation = Sort.by("dchosp").ascending();
		}
		else if (trie != null && trie.equalsIgnoreCase("DD")) {
			sortOperation = Sort.by("dchosp").descending();
		}
		else if (trie != null && trie.equalsIgnoreCase("HC")) {
			sortOperation = Sort.by("hosp").ascending();
		}
		else if (trie != null && trie.equalsIgnoreCase("HD")) {
			sortOperation = Sort.by("hosp").descending();
		}
		query.with(sortOperation);

		return PageableExecutionUtils.getPage(
				mongoTemplate.find(query, StatistiquesCovidDepartement.class),
				pageableAb, 
				() -> mongoTemplate.count(query.skip(0).limit(0), StatistiquesCovidDepartement.class));
	}

	@Override
	public List<StatistiquesCovidRegion> getStatistiquesRegionEvolution(String departementId, String regionId, Date dateDebut, Date dateFin, String trie, Integer topNVariation) {
		if (topNVariation == null || topNVariation <= 0) { // Limiter, par défaut, la sélection 
			topNVariation = DEFAULT_TOP_N;
		}

		Criteria criteria = new Criteria();
		if (dateDebut != null && dateFin != null) {
			criteria.andOperator(Criteria.where("date").gte(DateUtils.getFormattedFromDateTime(dateDebut)),
					Criteria.where("date").lte(DateUtils.getFormattedToDateTime(dateFin)));
		} 
		else if(dateDebut != null) {
			criteria.and("date").gte(DateUtils.getFormattedFromDateTime(dateDebut));
		} 
		else if(dateFin != null) {
			criteria.and("date").lte(DateUtils.getFormattedToDateTime(dateFin));
		}

		if(departementId != null && !departementId.trim().isEmpty()) {
			criteria.and("dep").is(departementId);
		}

		if(regionId != null && !regionId.trim().isEmpty()) {
			criteria.and("reg").is(regionId);
		}


		MatchOperation filterOperation = Aggregation.match(criteria);
		// TODO : Appliquer l'ordre de trie spécifié
		SortOperation sortOperation = Aggregation.sort(Sort.by("rad").ascending()); // 'CC' par défaut !
		// Traiter le cas où l'unité de temps est précisé !
		if (trie != null && trie.equalsIgnoreCase("CD")) {
			sortOperation = Aggregation.sort(Sort.by("rad").descending());
		}
		else if (trie != null && trie.equalsIgnoreCase("DC")) { // Décès à l'hopital
			sortOperation = Aggregation.sort(Sort.by("dchosp").ascending());
		}
		else if (trie != null && trie.equalsIgnoreCase("DD")) {
			sortOperation = Aggregation.sort(Sort.by("dchosp").descending());
		}
		else if (trie != null && trie.equalsIgnoreCase("HC")) {
			sortOperation = Aggregation.sort(Sort.by("hosp").ascending());
		}
		else if (trie != null && trie.equalsIgnoreCase("HD")) {
			sortOperation = Aggregation.sort(Sort.by("hosp").descending());
		}

		// Grouper les données par Région, ensuite effectuer des opérations d'Aggrégation :
		// - SOMME (SUM) pour les valeurs où il faut déterminer le Total comme le 'rea', 'rad', ...
		// - MOYENNE (AVG) pour les valeurs où c'est la moyenne qui est requise comme le 'R' = Facteur de reproduction du virus
		// - Sélection (LAST) de la dernière valeur pour la date
		Fields fieldsGroup = Aggregation.fields().and("reg")/*.and("lib_reg")*/;
		GroupOperation groupOperationCompter = Aggregation.group(fieldsGroup)
				.addToSet("dep").as("dep")
				.addToSet("lib_dep").as("lib_dep")
				.addToSet("lib_reg").as("lib_reg")
				.last("date").as("date") // Dernière date
				.avg("tx_pos").as("tx_pos") // Moyenne
				.avg("tx_incid").as("tx_incid") // Moyenne
				.avg("TO").as("TO") // Moyenne
				.avg("R").as("R") // La Moyenne
				.last("hosp").as("hosp") // Derniere observation
				.sum("rea").as("rea") // Somme
				.sum("rad").as("rad") // Somme
				.sum("dchosp").as("dchosp")
				.sum("reg_rea").as("reg_rea")
				.sum("incid_hosp").as("incid_hosp")
				.sum("incid_rea").as("incid_rea")
				.sum("incid_rad").as("incid_rad")
				.sum("incid_dchosp").as("incid_dchosp")
				.sum("reg_incid_rea").as("reg_incid_rea")
				.sum("pos").as("pos")
				.sum("pos_7j").as("pos_7j")
				.avg("cv_dose1").as("cv_dose1") // Moyenne
				.sum("esms_dc").as("esms_dc")
				.sum("esms_cas").as("esms_cas");
		ProjectionOperation projectToResultOperation = Aggregation.project()
				.andExpression("_id").as("reg")
				//.andExpression("_id.reg").as("reg")
				//.andExpression("_id.lib_reg").as("lib_reg")
				.andExpression("lib_reg").as("lib_reg")
				.andExpression("date").as("date")
				.andExpression("dep").as("dep")
				.andExpression("lib_dep").as("lib_dep")
				.andExpression("tx_pos").as("tx_pos")
				.andExpression("tx_incid").as("tx_incid")
				.andExpression("TO").as("TO")
				.andExpression("R").as("R")
				.andExpression("hosp").as("hosp")
				.andExpression("rea").as("rea")
				.andExpression("rad").as("rad")
				.andExpression("dchosp").as("dchosp")
				.andExpression("reg_rea").as("reg_rea")
				.andExpression("incid_hosp").as("incid_hosp")
				.andExpression("incid_rea").as("incid_rea")
				.andExpression("incid_rad").as("incid_rad")
				.andExpression("incid_dchosp").as("incid_dchosp")
				.andExpression("reg_incid_rea").as("reg_incid_rea")
				.andExpression("pos").as("pos")
				.andExpression("pos_7j").as("pos_7j")
				.andExpression("cv_dose1").as("cv_dose1")
				.andExpression("esms_dc").as("esms_dc")
				.andExpression("esms_cas").as("esms_cas");
		LimitOperation limitOperation = Aggregation.limit(topNVariation);
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupOperationCompter, sortOperation, limitOperation, projectToResultOperation);
		AggregationResults<StatistiquesCovidRegion> result = mongoTemplate.aggregate(aggregation, StatistiquesCovidDepartement.class, StatistiquesCovidRegion.class);
		List<StatistiquesCovidRegion> evolution = result.getMappedResults();
		return evolution;
	}

	@Override
	public StatistiquesCovidRegion getStatistiquesGlobales(String departementId, String regionId, Date dateDebut,
			Date dateFin) {
		Criteria criteria = new Criteria();
		if (dateDebut != null && dateFin != null) {
			criteria.andOperator(Criteria.where("date").gte(DateUtils.getFormattedFromDateTime(dateDebut)),
					Criteria.where("date").lte(DateUtils.getFormattedToDateTime(dateFin)));
		} 
		else if(dateDebut != null) {
			criteria.and("date").gte(DateUtils.getFormattedFromDateTime(dateDebut));
		} 
		else if(dateFin != null) {
			criteria.and("date").lte(DateUtils.getFormattedToDateTime(dateFin));
		}

		if(departementId != null && !departementId.trim().isEmpty()) {
			criteria.and("dep").is(departementId);
		}

		if(regionId != null && !regionId.trim().isEmpty()) {
			criteria.and("reg").is(regionId);
		}


		MatchOperation filterOperation = Aggregation.match(criteria);

		// Grouper les données par Département, ensuite effectuer des opérations d'Aggrégation :
		// - SOMME (SUM) pour les valeurs où il faut déterminer le Total comme le 'rea', 'rad', ...
		// - MOYENNE (AVG) pour les valeurs où c'est la moyenne qui est requise comme le 'R' = Facteur de reproduction du virus
		// - Sélection (LAST) de la dernière valeur pour la date
		Fields fieldsGroup = Aggregation.fields();
		GroupOperation groupOperationCompter = Aggregation.group(fieldsGroup)
				.addToSet("dep").as("dep")
				.addToSet("reg").as("reg")
				.addToSet("lib_dep").as("lib_dep")
				.addToSet("lib_reg").as("lib_reg")
				.last("date").as("date") // Dernière date
				.avg("tx_pos").as("tx_pos") // Moyenne
				.avg("tx_incid").as("tx_incid") // Moyenne
				.avg("TO").as("TO") // Moyenne
				.avg("R").as("R") // La Moyenne
				.last("hosp").as("hosp") // Derniere observation
				.sum("rea").as("rea") // Somme
				.sum("rad").as("rad") // Somme
				.sum("dchosp").as("dchosp")
				.sum("reg_rea").as("reg_rea")
				.sum("incid_hosp").as("incid_hosp")
				.sum("incid_rea").as("incid_rea")
				.sum("incid_rad").as("incid_rad")
				.sum("incid_dchosp").as("incid_dchosp")
				.sum("reg_incid_rea").as("reg_incid_rea")
				.sum("pos").as("pos")
				.sum("pos_7j").as("pos_7j")
				.avg("cv_dose1").as("cv_dose1") // Moyenne
				.sum("esms_dc").as("esms_dc")
				.sum("esms_cas").as("esms_cas");
		ProjectionOperation projectToResultOperation = Aggregation.project()
				//.andExpression("_id.dep").as("dep")
				//.andExpression("_id.reg").as("reg")
				//.andExpression("reg").as("reg")
				//.andExpression("dep").as("dep")
				//.andExpression("lib_dep").as("lib_dep")
				//.andExpression("lib_reg").as("lib_reg")
				.andExpression("date").as("date")
				.andExpression("tx_pos").as("tx_pos")
				.andExpression("tx_incid").as("tx_incid")
				.andExpression("TO").as("TO")
				.andExpression("R").as("R")
				.andExpression("hosp").as("hosp")
				.andExpression("rea").as("rea")
				.andExpression("rad").as("rad")
				.andExpression("dchosp").as("dchosp")
				.andExpression("reg_rea").as("reg_rea")
				.andExpression("incid_hosp").as("incid_hosp")
				.andExpression("incid_rea").as("incid_rea")
				.andExpression("incid_rad").as("incid_rad")
				.andExpression("incid_dchosp").as("incid_dchosp")
				.andExpression("reg_incid_rea").as("reg_incid_rea")
				.andExpression("pos").as("pos")
				.andExpression("pos_7j").as("pos_7j")
				.andExpression("cv_dose1").as("cv_dose1")
				.andExpression("esms_dc").as("esms_dc")
				.andExpression("esms_cas").as("esms_cas");
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupOperationCompter, projectToResultOperation);
		AggregationResults<StatistiquesCovidRegion> result = mongoTemplate.aggregate(aggregation, StatistiquesCovidDepartement.class, StatistiquesCovidRegion.class);
		StatistiquesCovidRegion resume = result.getUniqueMappedResult();
		return resume;
	}

}
