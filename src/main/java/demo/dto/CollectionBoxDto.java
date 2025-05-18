package demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollectionBoxDto {
    private Long id;
    private String assignmentStatus;
    private boolean empty;
}