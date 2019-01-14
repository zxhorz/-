package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.AbbriviationDictCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:27:43 AM
 */
@Repository
public interface AbbriviationDictCorpusRepository extends PagingAndSortingRepository<AbbriviationDictCorpus, Integer> {
	
	@Query(value = "select p from AbbriviationDictCorpus p where p.businessDomainId = :domain order by p.id")
	Page<AbbriviationDictCorpus> findByDomainPageable(@Param("domain") int domain, Pageable pageable);

	@Query(value = "select p from AbbriviationDictCorpus p where p.systemId = :system order by p.id")
	Page<AbbriviationDictCorpus> findBySystemPageable(@Param("system") int system, Pageable pageable);

	@Query(value = "select p from AbbriviationDictCorpus p where p.organizationId = :organization order by p.id")
	Page<AbbriviationDictCorpus> findByOrganizationPageable(@Param("organization") int organization, Pageable pageable);

	@Query(value = "select p from AbbriviationDictCorpus p where p.businessDomainId = :domain and p.systemId = :system order by p.id")
	Page<AbbriviationDictCorpus> findByDomainAndSystemPageable(@Param("domain") int domain,
			@Param("system") int system, Pageable pageable);
	
	@Query(value = "select p from AbbriviationDictCorpus p where p.businessDomainId = :domain and p.organizationId = :organization order by p.id")
	Page<AbbriviationDictCorpus> findByDomainAndOrganizationPageable(@Param("domain") int domain,
			@Param("organization") int organization, Pageable pageable);

	@Query(value = "select p from AbbriviationDictCorpus p where p.systemId = :system and p.organizationId = :organization order by p.id")
	Page<AbbriviationDictCorpus> findBySystemAndOrganizationPageable(@Param("system") int system,
			@Param("organization") int organization, Pageable pageable);
	
	@Query(value = "select p from AbbriviationDictCorpus p where p.businessDomainId = :domain and p.organizationId = :organization and p.systemId = :system order by p.id")
	Page<AbbriviationDictCorpus> findByDomainAndSystemAndOrganizationPageable(@Param("domain") int domain, 
			@Param("system") int system, @Param("organization") int organization, Pageable pageable);

	@Query(value = "select p from AbbriviationDictCorpus p where p.businessDomainId = :domain order by p.id")
	List<AbbriviationDictCorpus> findByDomain(@Param("domain") int domain);

	@Query(value = "select p from AbbriviationDictCorpus p where p.systemId = :system order by p.id")
	List<AbbriviationDictCorpus> findBySystem(@Param("system") int system);

	@Query(value = "select p from AbbriviationDictCorpus p where p.organizationId = :organization order by p.id")
	List<AbbriviationDictCorpus> findByOrganization(@Param("organization") int organization);

	@Query(value = "select p from AbbriviationDictCorpus p where p.businessDomainId = :domain and p.systemId = :system order by p.id")
	List<AbbriviationDictCorpus> findByDomainAndSystem(@Param("domain") int domain, @Param("system") int system);

	@Query(value = "select p from AbbriviationDictCorpus p where p.businessDomainId = :domain and p.organizationId = :organization order by p.id")
	List<AbbriviationDictCorpus> findByDomainAndOrganization(@Param("domain") int domain, @Param("organization") int organization);

	@Query(value = "select p from AbbriviationDictCorpus p where p.systemId = :system and p.organizationId = :organization order by p.id")
	List<AbbriviationDictCorpus> findBySystemAndOrganization(@Param("system") int system, @Param("organization") int organization);

	@Query(value = "select p from AbbriviationDictCorpus p where p.businessDomainId = :domain and p.organizationId = :organization and p.systemId = :system order by p.id")
	List<AbbriviationDictCorpus> findByDomainAndSystemAndOrganization(@Param("domain") int domain, @Param("system") int system, @Param("organization") int organization);
	
}
