package com.yifan.dev;

import net.openhft.lang.model.constraints.MaxSize;

/**
 * Created by yifanzhang.
 */
public interface BondVOInterface {
    int getId();

    void setId(int id);

    long getIssueDate();

    void setIssueDate(long issueDate);  /* time in millis */

    long getMaturityDate();

    void setMaturityDate(long maturityDate);  /* time in millis */

    long addAtomicMaturityDate(long toAdd);

    boolean compareAndSwapCoupon(double expected, double value);

    double getCoupon();

    void setCoupon(double coupon);

    long getQuantity();

    void setQuantity(long quantity);

    double addAtomicCoupon(double toAdd);

    String getSymbol();

    void setSymbol(@MaxSize(20) String symbol);

    // OpenHFT Off-Heap array[ ] processing notice ‘At’ suffix
    void setMarketPxIntraDayHistoryAt(@MaxSize(7) int tradingDayHour, MarketPx mPx);

    /* 7 Hours in the Trading Day:
     * index_0 = 9.30am,
     * index_1 = 10.30am,
     …,
     * index_6 = 4.30pm
     */

    MarketPx getMarketPxIntraDayHistoryAt(int tradingDayHour);

    /* nested interface - empowering an Off-Heap hierarchical “TIER of prices”
    as array[ ] value */
    interface MarketPx {
        double getCallPx();

        void setCallPx(double px);

        double getParPx();

        void setParPx(double px);

        double getMaturityPx();

        void setMaturityPx(double px);

        double getBidPx();

        void setBidPx(double px);

        double getAskPx();

        void setAskPx(double px);
    }
}
