CREATE PROCEDURE spApproveRequest
	@Username VARCHAR(100)
AS
BEGIN
	DECLARE @IdVehicle INTEGER

	SELECT TOP 1 @IdVehicle = Vehicle
	FROM Request
	WHERE Username = @Username

	DELETE FROM Request
	WHERE Username = @Username

	INSERT INTO Courier (Username, NumberDelivered, Profit, Status, Vehicle) 
	VALUES (@Username, 0, 0, 0, @IdVehicle)
END
GO
