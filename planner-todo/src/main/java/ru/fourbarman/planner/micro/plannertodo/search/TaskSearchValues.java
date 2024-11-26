package ru.fourbarman.planner.micro.plannertodo.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskSearchValues {

    //поля поиска
    private String title;
    private Integer completed;
    private Long priorityId;
    private Long categoryId;
    private String userId;
    private Date dateFrom; // для того, чтобы задать периода по датам
    private Date dateTo;

    // постраничность
    private Integer pageNumber;
    private Integer pageSize;

    //сортировка
    private String sortColumn;
    private String sortDirection;
}
