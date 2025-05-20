package EtfRecommendService.restAPI;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<SnapshotEntity,Long> {
}
