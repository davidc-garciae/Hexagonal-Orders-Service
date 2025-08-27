package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ListOrdersByStatusUseCaseTest {

  private IOrderPersistencePort persistence;
  private ListOrdersByStatusUseCase useCase;

  @BeforeEach
  void setup() {
    persistence = Mockito.mock(IOrderPersistencePort.class);
    useCase = new ListOrdersByStatusUseCase(persistence);
  }

  @Test
  @DisplayName("Should return paged orders when parameters are valid")
  void ok() {
    PagedResult<Order> page = new PagedResult<>(List.of(new Order()), 0, 10, 1, 1);
    when(persistence.findByRestaurantAndStatus(1L, OrderStatus.PENDIENTE, 0, 10)).thenReturn(page);

    var result = useCase.listByStatusAndRestaurant(1L, OrderStatus.PENDIENTE, 0, 10);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("Should validate inputs")
  void validate() {
    assertThatThrownBy(() -> useCase.listByStatusAndRestaurant(null, OrderStatus.PENDIENTE, 0, 10))
        .isInstanceOf(DomainException.class);
    assertThatThrownBy(() -> useCase.listByStatusAndRestaurant(1L, null, 0, 10))
        .isInstanceOf(DomainException.class);
    assertThatThrownBy(() -> useCase.listByStatusAndRestaurant(1L, OrderStatus.PENDIENTE, -1, 10))
        .isInstanceOf(DomainException.class);
    assertThatThrownBy(() -> useCase.listByStatusAndRestaurant(1L, OrderStatus.PENDIENTE, 0, 0))
        .isInstanceOf(DomainException.class);
  }
}
