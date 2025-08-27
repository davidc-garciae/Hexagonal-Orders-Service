package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderItem;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CreateOrderUseCaseTest {

  private IOrderPersistencePort persistence;
  private CreateOrderUseCase useCase;

  @BeforeEach
  void setup() {
    persistence = Mockito.mock(IOrderPersistencePort.class);
    useCase = new CreateOrderUseCase(persistence);
  }

  private Order buildValidOrder() {
    Order o = new Order();
    o.setCustomerId(10L);
    o.setRestaurantId(20L);
    o.setItems(List.of(new OrderItem(100L, 2)));
    return o;
  }

  @Test
  @DisplayName("Should create order with status PENDIENTE when request is valid")
  void createOrder_ok() {
    Order request = buildValidOrder();
    when(persistence.customerHasActiveOrder(10L)).thenReturn(false);
    when(persistence.allDishesBelongToRestaurant(20L, List.of(100L))).thenReturn(true);
    when(persistence.save(any(Order.class)))
        .thenAnswer(
            inv -> {
              Order saved = inv.getArgument(0);
              saved.setId(1L);
              return saved;
            });

    Order result = useCase.createOrder(request);

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDIENTE);
    assertThat(result.getItems()).hasSize(1);
  }

  @Test
  @DisplayName("Should fail when customer has active order")
  void createOrder_conflict_activeOrder() {
    Order request = buildValidOrder();
    when(persistence.customerHasActiveOrder(10L)).thenReturn(true);

    assertThatThrownBy(() -> useCase.createOrder(request))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("active order");
  }

  @Test
  @DisplayName("Should fail when dishes do not belong to restaurant")
  void createOrder_invalidDishesRestaurant() {
    Order request = buildValidOrder();
    when(persistence.customerHasActiveOrder(10L)).thenReturn(false);
    when(persistence.allDishesBelongToRestaurant(20L, List.of(100L))).thenReturn(false);

    assertThatThrownBy(() -> useCase.createOrder(request))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("dishes must belong");
  }

  @Test
  @DisplayName("Should validate required fields")
  void createOrder_validation() {
    assertThatThrownBy(() -> useCase.createOrder(null)).isInstanceOf(DomainException.class);

    Order empty = new Order();
    assertThatThrownBy(() -> useCase.createOrder(empty)).isInstanceOf(DomainException.class);

    empty.setCustomerId(1L);
    assertThatThrownBy(() -> useCase.createOrder(empty)).isInstanceOf(DomainException.class);

    empty.setRestaurantId(2L);
    assertThatThrownBy(() -> useCase.createOrder(empty)).isInstanceOf(DomainException.class);

    empty.setItems(List.of(new OrderItem(null, 1)));
    assertThatThrownBy(() -> useCase.createOrder(empty)).isInstanceOf(DomainException.class);
  }
}
