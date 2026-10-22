package com.app.demo.repo;

import com.app.demo.entity.Candidate;
import com.app.demo.entity.Category;
import com.app.demo.entity.Stage;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchDao {



    public static Specification<Candidate> findAllBy(
            String keyword,
            String dataField,
            String category ,
            String stage ,
            LocalDate start ,
            LocalDate end){

        return (root, query, cb) -> {
            List<Predicate> predicates =  new ArrayList<>();
            if (keyword != null){
                String term = '%' + keyword.toLowerCase().trim() + '%' ;
                Predicate firstname = cb.like(cb.lower(root.get("firstName")) , term);
                Predicate lastName = cb.like(cb.lower(root.get("lastName")) , term);
                Predicate phone = cb.like(cb.lower(root.get("phone")) , term);

                predicates.add(cb.or(firstname,lastName,phone));
            }
            if(stage!= null) {
                try {
                    predicates.add(cb.equal(root.get("stage"), Stage.valueOf(stage.toUpperCase())));
                } catch (IllegalArgumentException e) {
                    return cb.disjunction();
                }
            }
            if (category!=null) {
                try {
                    predicates.add(cb.equal(root.get("category"), Category.valueOf(category.toUpperCase())));
                } catch (IllegalArgumentException e) {
                    return cb.disjunction();
                }
            }
            if (dataField != null && (start != null || end != null)){
                Path<LocalDate> dataPath ;
                Predicate dateP ;
                if (dataField.equalsIgnoreCase("registerDate")){
                    dataPath = root.get("infoModel").get("registerDate");
                }else if (dataField.equalsIgnoreCase("birthDate")||dataField.equalsIgnoreCase("circulationDate")||dataField.equalsIgnoreCase("creneauDate")||dataField.equalsIgnoreCase("codeDate")){
                    dataPath = root.get("infoModel").get(dataField);
                }else return cb.disjunction();
                 if (start != null &&  end != null){
                     dateP = cb.between(dataPath,start,end);
                 }else if (start != null)
                     dateP =  cb.greaterThanOrEqualTo(dataPath,start);
                 else
                    dateP  = cb.lessThanOrEqualTo(dataPath ,end);
                        predicates.add(dateP);
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

    }
}
