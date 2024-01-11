package ru.practicum.ewm.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.compilation.model.EventCompilationConnection;

@Repository
public interface EventCompilationConnectionRepository extends JpaRepository<EventCompilationConnection, Integer> {

}
