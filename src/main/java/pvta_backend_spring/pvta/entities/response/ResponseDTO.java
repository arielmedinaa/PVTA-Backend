package pvta_backend_spring.pvta.entities.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class ResponseDTO<T> {
    String messageResponse = "";
    long totalRegistros = 0;
    T dataResponse;
}
