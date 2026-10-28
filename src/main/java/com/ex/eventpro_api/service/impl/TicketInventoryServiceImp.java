package com.ex.eventpro_api.service.impl;


import com.ex.eventpro_api.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.exception.InsufficientStockException;
import com.ex.eventpro_api.exception.ResourceNotFoundException;
import com.ex.eventpro_api.repository.TicketCategoriesPerEventRepository;
import com.ex.eventpro_api.service.TicketInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TicketInventoryServiceImp implements TicketInventoryService {

    private final TicketCategoriesPerEventRepository ticketCategoryRepository;
    @Override
    public TicketCategoriesPerEvent lockAndDecrementStock(Long ticketCategoriesPerEventId , Integer quantity){

        TicketCategoriesPerEvent stock = ticketCategoryRepository.findByIdWithLock(ticketCategoriesPerEventId)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie de billet introuvable avec l'ID : " + ticketCategoriesPerEventId));

        if(quantity > stock.getTotalQuantity())
            throw new InsufficientStockException("Stock insuffisant pour la catégorie " + stock.getCategory().getName() + " Places restantes : " + stock.getTotalQuantity());

        stock.setTotalQuantity(stock.getTotalQuantity() - quantity);

        return ticketCategoryRepository.save(stock);
    }

    @Override
    public void releaseStock(Long ticketCategoriesPerEventId,Integer quantity){
        TicketCategoriesPerEvent stock = ticketCategoryRepository.findById(ticketCategoriesPerEventId)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie de billet introuvable avec l'ID : " + ticketCategoriesPerEventId));

        stock.setTotalQuantity(stock.getTotalQuantity() + quantity);

        ticketCategoryRepository.save(stock);
    }
}
