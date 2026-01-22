-- KEYS[1] = users key
-- KEYS[2] = stock key
-- ARGV[1] = userId

if redis.call("SISMEMBER", KEYS[1], ARGV[1]) == 1 then
  return -1
end

local stock = tonumber(redis.call("GET", KEYS[2]))
if not stock or stock <= 0 then
  return 0  -- SOLD_OUT
end

redis.call("DECR", KEYS[2])

redis.call("SADD", KEYS[1], ARGV[1])

return 1 -- SUCCESS