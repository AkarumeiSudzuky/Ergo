package com.ergo.Springserver.model.team;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Integer> {
    Team findTeamByName(String name);

    Optional<Team> findTopByOrderByIdDesc();

    Optional<Team> findById(Long id);
}
