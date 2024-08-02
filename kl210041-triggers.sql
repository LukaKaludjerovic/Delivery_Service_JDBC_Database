CREATE TRIGGER TR_TransportOffer_Accept
   ON Package
   AFTER UPDATE
AS 
BEGIN
	DECLARE @IdPackage INTEGER

	SELECT @IdPackage = I.IdPackage
	FROM inserted I JOIN deleted D ON I.IdPackage = D.IdPackage
	WHERE D.Status = 0 AND I.Status = 1

	DELETE FROM Offer
	WHERE Package = @IdPackage
END
GO
