package com.hengtiansoft.bluemorpho.workbench.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hengtiansoft.bluemorpho.workbench.domain.corpus.WordAndPhraseTagCorpus;

/**
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 22, 2018 11:31:47 AM
 */
@Repository
public interface WordAndPhraseTagCorpusRepository extends PagingAndSortingRepository<WordAndPhraseTagCorpus, Integer> {

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.businessDomainId = :domain order by p.id")
	Page<WordAndPhraseTagCorpus> findByDomainPageable(@Param("domain") int domain, Pageable pageable);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.systemId = :system order by p.id")
	Page<WordAndPhraseTagCorpus> findBySystemPageable(@Param("system") int system, Pageable pageable);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.organizationId = :organization order by p.id")
	Page<WordAndPhraseTagCorpus> findByOrganizationPageable(@Param("organization") int organization, Pageable pageable);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.businessDomainId = :domain and p.systemId = :system order by p.id")
	Page<WordAndPhraseTagCorpus> findByDomainAndSystemPageable(@Param("domain") int domain,
			@Param("system") int system, Pageable pageable);
	
	@Query(value = "select p from WordAndPhraseTagCorpus p where p.businessDomainId = :domain and p.organizationId = :organization order by p.id")
	Page<WordAndPhraseTagCorpus> findByDomainAndOrganizationPageable(@Param("domain") int domain,
			@Param("organization") int organization, Pageable pageable);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.systemId = :system and p.organizationId = :organization order by p.id")
	Page<WordAndPhraseTagCorpus> findBySystemAndOrganizationPageable(@Param("system") int system,
			@Param("organization") int organization, Pageable pageable);
	
	@Query(value = "select p from WordAndPhraseTagCorpus p where p.businessDomainId = :domain and p.organizationId = :organization and p.systemId = :system order by p.id")
	Page<WordAndPhraseTagCorpus> findByDomainAndSystemAndOrganizationPageable(
			@Param("domain") int domain, @Param("system") int system, @Param("organization") int organization, Pageable pageable);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.businessDomainId = :domain order by p.id")
	List<WordAndPhraseTagCorpus> findByDomain(@Param("domain") int domain);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.systemId = :system order by p.id")
	List<WordAndPhraseTagCorpus> findBySystem(@Param("system") int system);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.organizationId = :organization order by p.id")
	List<WordAndPhraseTagCorpus> findByOrganization(@Param("organization") int organization);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.businessDomainId = :domain and p.systemId = :system order by p.id")
	List<WordAndPhraseTagCorpus> findByDomainAndSystem(@Param("domain") int domain, @Param("system") int system);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.businessDomainId = :domain and p.organizationId = :organization order by p.id")
	List<WordAndPhraseTagCorpus> findByDomainAndOrganization(@Param("domain") int domain, @Param("organization") int organization);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.systemId = :system and p.organizationId = :organization order by p.id")
	List<WordAndPhraseTagCorpus> findBySystemAndOrganization(@Param("system") int system, @Param("organization") int organization);

	@Query(value = "select p from WordAndPhraseTagCorpus p where p.businessDomainId = :domain and p.organizationId = :organization and p.systemId = :system order by p.id")
	List<WordAndPhraseTagCorpus> findByDomainAndSystemAndOrganization(@Param("domain") int domain, @Param("system") int system, @Param("organization") int organization);

}
