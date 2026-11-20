# NextStore AI Inventory Forecaster & Retail Intelligence

Calculates inventory consumption velocity, runout estimation, and reorder alerts.

## Reorder Recommendations
Flags items whose current stock is projected to deplete before supplier lead time.

## Runout Estimates
Dynamic banner showing 'Runs out in X days' based on average daily units sold.

## Velocity Tiers
- Fast-Moving: Sales velocity > 5 units/day
- Medium-Moving: 1 - 5 units/day
- Slow-Moving: < 1 unit/day

## Inventory Health Gauge
Visual radial indicator illustrating ratio of healthy stock vs low stock.

## Margin Metrics
Computes gross profit margins `(Price - Cost) / Price` for overall store inventory.

## Dead Stock Alerts
Flags items with zero sales movements in the last 30 days to prevent capital lockup.
