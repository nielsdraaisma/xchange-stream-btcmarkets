package info.bitrich.xchangestream.btcmarkets.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import info.bitrich.xchangestream.btcmarkets.BTCMarketsStreamingAdapters;
import info.bitrich.xchangestream.btcmarkets.dto.BTCMarketsWebSocketOrderbookMessage;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;

public class BTCMarketsStreamingMarketDataService implements StreamingMarketDataService {

  private static final String CHANNEL_ORDERBOOK = "orderbook";

  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  private final BTCMarketsStreamingService service;

  public BTCMarketsStreamingMarketDataService(BTCMarketsStreamingService service) {
    this.service = service;
  }

  private OrderBook handleOrderbookMessage(BTCMarketsWebSocketOrderbookMessage message)
      throws InvalidFormatException {
    return BTCMarketsStreamingAdapters.adaptOrderbookMessageToOrderbook(message);
  }

  @Override
  public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
    String marketId = BTCMarketsStreamingAdapters.adaptCurrencyPairToMarketId(currencyPair);
    return service
        .subscribeChannel(CHANNEL_ORDERBOOK, marketId)
        .map(
            node -> {
              BTCMarketsWebSocketOrderbookMessage orderEvent =
                  mapper.treeToValue(node, BTCMarketsWebSocketOrderbookMessage.class);
              return this.handleOrderbookMessage(orderEvent);
            });
  }

  @Override
  public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
    throw new NotAvailableFromExchangeException();
  }

  @Override
  public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
    throw new NotAvailableFromExchangeException();
  }
}
