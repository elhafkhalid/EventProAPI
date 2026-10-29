package com.ex.eventpro_api.payment.mapper;

import com.ex.eventpro_api.payment.dto.response.PaymentResponse;
import com.ex.eventpro_api.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "reservationId", source = "reservation.id")
    PaymentResponse toResponse(Payment payment);
}
