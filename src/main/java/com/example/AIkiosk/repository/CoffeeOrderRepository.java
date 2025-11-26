package com.example.AIkiosk.repository;

import com.example.AIkiosk.dto.FaceWithCoffeeDTO;
import com.example.AIkiosk.entity.CoffeeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CoffeeOrderRepository extends JpaRepository<CoffeeOrder, Long> {

    @Query(value = """
            SELECT df.image_path AS imagePath, c.name AS coffeeName
            FROM coffee_order o
            JOIN detected_faces df ON o.face_id = df.id
            JOIN coffee_menu c ON o.coffee_id = c.id
            JOIN (
                SELECT face_id, MAX(order_time) AS max_order_time
                FROM coffee_order
                WHERE face_id IN :faceIds
                GROUP BY face_id
            ) latest ON o.face_id = latest.face_id AND o.order_time = latest.max_order_time
            ORDER BY latest.max_order_time DESC
            """, nativeQuery = true)
    List<Object[]> findLatestFaceImagesAndCoffeeByFaceIds(@Param("faceIds") List<Integer> faceIds);

    default List<FaceWithCoffeeDTO> findLatestFaceWithCoffeeDTOByFaceIds(List<Integer> faceIds) {
        List<Object[]> results = findLatestFaceImagesAndCoffeeByFaceIds(faceIds);
        List<FaceWithCoffeeDTO> dtoList = new ArrayList<>();
        for (Object[] row : results) {
            dtoList.add(new FaceWithCoffeeDTO((String) row[0], (String) row[1]));
        }
        return dtoList;
    }
}
