package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.Order;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DeliverOrderUseCaseTest {

  private IOrderPersistencePort persistence;
  private DeliverOrderUseCase useCase;

  @BeforeEach
  void setup() {
    persistence = Mockito.mock(IOrderPersistencePort.class);
    useCase = new DeliverOrderUseCase(persistence);
  }

  @Test
  @DisplayName("Should deliver when status LISTO and pin matches")
  void ok() {
    Order o = new Order();
    o.setId(1L);
    o.setStatus(OrderStatus.LISTO);
    o.setPin("123456");
    when(persistence.findById(1L)).thenReturn(Optional.of(o));
    when(persistence.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

    Order r = useCase.deliver(1L, "123456");
    assertThat(r.getStatus()).isEqualTo(OrderStatus.ENTREGADO);
  }

  @Test
  @DisplayName("Should fail when wrong pin or invalid status")
  void errors() {
    Order o = new Order();
    o.setId(1L);
    o.setStatus(OrderStatus.LISTO);
    o.setPin("123456");
    when(persistence.findById(1L)).thenReturn(Optional.of(o));

    assertThatThrownBy(() -> useCase.deliver(1L, "0000")).isInstanceOf(DomainException.class);

    o.setStatus(OrderStatus.PENDIENTE);
    assertThatThrownBy(() -> useCase.deliver(1L, "123456")).isInstanceOf(DomainException.class);
  }
}
