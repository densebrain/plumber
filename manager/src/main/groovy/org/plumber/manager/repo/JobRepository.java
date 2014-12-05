package org.plumber.manager.repo;


import org.plumber.manager.domain.Job;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by jglanz on 9/25/14.
 */

public interface JobRepository extends PagingAndSortingRepository<Job, String> {




}
